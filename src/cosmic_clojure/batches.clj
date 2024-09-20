(ns cosmic-clojure.batches
  (:require [malli.core :as m]))

(def OrderLine
  [:map
   [:OrderLine/order-id string?]
   [:OrderLine/sku string?]
   [:OrderLine/quantity int?]])

(defn ->order-line
  "A factory function to create an order line."
  [{:keys [order-id sku quantity]}]
  (m/validate OrderLine
              {:OrderLine/order-id order-id
               :OrderLine/sku sku
               :OrderLine/quantity quantity}))

(def Batch
  [:map
   [:Batch/reference string?]
   [:Batch/sku string?]
   [:Batch/quantity int?]
   [:Batch/eta [:maybe inst?]]])

(defn ->batch
  "A factory function to create a batch."
  [{:keys [reference sku quantity eta]}]
  (m/validate Batch
              {:Batch/reference reference
               :Batch/sku sku
               :Batch/quantity quantity
               :Batch/eta eta}))

(defn available-quantity
  "Returns the number of items still available in the batch."
  [batch]
  (:Batch/quantity batch))

(defn allocate-line
  "Allocate an order line to a batch. Returns a batch with an updated
  quantity."
  [batch line]
  (-> batch
      (update :Batch/quantity - (:OrderLine/quantity line))))

(defn can-allocate
  "Check if a batch can be allocated to an order line."
  [batch line]
  (let [available (available-quantity batch)]
    (and (>= available (:OrderLine/quantity line))
         (not= 0 available)
         (= (:Batch/sku batch) (:OrderLine/sku line)))))
