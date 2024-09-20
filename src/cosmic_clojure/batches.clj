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
            {:OrderLine/order-id order-id
             :OrderLine/sku sku
             :OrderLine/quantity quantity}))

(def Batch
  [:map
   [:Batch/reference string?]
   [:Batch/sku string?]
   [:Batch/quantity int?]
   [:Batch/eta [:maybe inst?]]
   [:Batch/allocations [:set {:default #{}} #'OrderLine]]])

(defn ->batch
  "A factory function to create a batch."
  [{:keys [reference sku quantity eta]}]
  (m/coerce Batch
            {:Batch/reference reference
             :Batch/sku sku
             :Batch/quantity quantity
             :Batch/eta eta
             :Batch/allocations #{}}))

(defn available-quantity
  "Returns the number of items still available in the batch."
  [batch]
  (:Batch/quantity batch))

(defn allocate-line
  "Allocate an order line to a batch. Returns a batch with an updated
  quantity."
  [batch line]
  (prn "Allocating line: " line " to batch: " batch)
  (-> batch
      (update :Batch/quantity - (:OrderLine/quantity line))))

(defn can-allocate
  "Check if a batch can be allocated to an order line."
  [batch line]
  (let [available (available-quantity batch)]
    (and (>= available (:OrderLine/quantity line))
         (not= 0 available)
         (= (:Batch/sku batch) (:OrderLine/sku line)))))

(defn deallocate-line
  "Deallocate an order line from a batch. 
   Returns a batch with an updated quantity."
  [batch line]
  (let [allocations (:Batch/allocations batch)
        line-exists? (contains? allocations line)]
    (if line-exists?
      (-> batch
          (update :Batch/allocations disj line)
          (update :Batch/quantity + (:OrderLine/quantity line)))
      batch)))

