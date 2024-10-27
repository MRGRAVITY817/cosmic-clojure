(ns cosmic-clojure.batch.batches
  (:require [malli.core :as m]))

(def OrderLine
  "A value object representing an order line."
  [:map
   [:OrderLine/order-id string?]
   [:OrderLine/sku string?]
   [:OrderLine/quantity int?]])

(defn ->order-line
  "A factory function to create an order line."
  [{:keys [order-id sku quantity]}]
  (m/coerce OrderLine
            {:OrderLine/order-id order-id,
             :OrderLine/sku      sku,
             :OrderLine/quantity quantity}))

(def ^:private LineAllocations
  [:set {:default #{}}
   #'OrderLine])

(def Batch
  "A value object representing a batch."
  [:map
   [:Batch/reference string?]
   [:Batch/sku string?]
   [:Batch/purchased-quantity int?]
   [:Batch/eta [:maybe inst?]]
   [:Batch/allocations LineAllocations]])

(defn ->batch
  "A factory function to create a batch."
  [{:keys [reference sku quantity eta]}]
  (m/coerce Batch
            {:Batch/reference   reference,
             :Batch/sku         sku,
             :Batch/purchased-quantity quantity,
             :Batch/eta         eta,
             :Batch/allocations #{}}))

(defn allocated-quantity
  "Returns the total quantity of allocated order lines in batch."
  [batch]
  (->> (:Batch/allocations batch)
       (map :OrderLine/quantity)
       (reduce + 0)))

(defn available-quantity
  "Returns the number of items still available in the batch."
  [batch]
  (- (:Batch/purchased-quantity batch) (allocated-quantity batch)))

(defn can-allocate
  "Check if a batch can be allocated to an order line."
  [batch line]
  (let [available (available-quantity batch)]
    (and (>= available (:OrderLine/quantity line))
         (not= 0 available)
         (= (:Batch/sku batch) (:OrderLine/sku line))
         (not (contains? (:Batch/allocations batch) line)))))

(defn allocate-line
  "Allocate an order line to a batch. Returns a batch with an updated quantity. 
   If the line has already been allocated to this batch, it will be ignored."
  [batch line]
  (if (can-allocate batch line)
    (-> batch
        (update :Batch/allocations conj line))
    batch))

(defn allocate-line-to-preferred-batch
  "Allocate an order line to one of the supplied batches.
   Batches that matches following conditions will be preferred:
   - batch which is in stock (over shipped)
   - batch with earliest eta

   Returns updated batches.
   "
  [batches line]
  (let [valid-batches (filter #(can-allocate % line) batches)]
    (if (empty? valid-batches)
      {:allocated nil
       :error     "Out of stock"}
      (let [preferred (first (sort-by :Batch/eta valid-batches))]
        {:allocated (allocate-line preferred line),
         :error     nil}))))

(defn deallocate-line
  "Deallocate an order line from a batch. 
   Returns a batch with an updated quantity."
  [batch line]
  (if (contains? (:Batch/allocations batch) line)
    (-> batch
        (update :Batch/allocations disj line))
    batch))

;; Query functions
(defn reference [batch] (:Batch/reference batch))
