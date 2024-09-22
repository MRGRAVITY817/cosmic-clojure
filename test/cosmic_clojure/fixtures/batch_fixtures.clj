(ns cosmic-clojure.fixtures.batch-fixtures
  (:require [cosmic-clojure.batches :refer [Batch OrderLine]]
            [malli.generator :as mg]))

(defn day-after
  [days]
  (let [calendar (java.util.Calendar/getInstance)
        _ (.add calendar java.util.Calendar/DAY_OF_YEAR days)]
    (.getTime calendar)))

(defn now [] (day-after 0))

(defn batch-fixture
  [& {:keys [reference sku quantity eta]}]
  (-> (mg/generate Batch)
      (update :Batch/reference #(or reference %))
      (update :Batch/sku #(or sku %))
      (update :Batch/purchased-quantity #(or quantity %))
      (update :Batch/eta #(or eta %))
      (assoc :Batch/allocations #{})))

(defn order-line-fixture
  [& {:keys [order-id sku quantity]}]
  (-> (mg/generate OrderLine)
      (update :OrderLine/order-id #(or order-id %))
      (update :OrderLine/sku #(or sku %))
      (update :OrderLine/quantity #(or quantity %))))

(defn ->batch-and-line
  "Test helper function that creates a batch and order line from given input."
  [{:keys [sku batch-qty line-qty]}]
  [(batch-fixture {:sku sku, :quantity batch-qty})
   (order-line-fixture {:sku sku, :quantity line-qty})])

