(ns cosmic-clojure.batches
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

(def Batch
  [:map
   [:Batch/reference string?]
   [:Batch/sku string?]
   [:Batch/purchased-quantity int?]
   [:Batch/eta [:maybe inst?]]
   [:Batch/allocations
    [:set {:default #{}}
     #'OrderLine]]])

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
  (prn {:batch batch, :line line})
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

(defn deallocate-line
  "Deallocate an order line from a batch. 
   Returns a batch with an updated quantity."
  [batch line]
  (if (contains? (:Batch/allocations batch) line)
    (-> batch
        (update :Batch/allocations disj line))
    batch))

