(ns cosmic-clojure.batches-test
  (:require [clojure.test :refer [deftest is testing]]
            [cosmic-clojure.batches :as batches :refer [Batch OrderLine]]
            [malli.generator :as mg]))

;; Test helpers

(defn- now [] (new java.util.Date))

(defn- batch-fixture
  [& {:keys [reference sku quantity eta]}]
  (-> (mg/generate Batch)
      (update :Batch/reference #(or reference %))
      (update :Batch/sku #(or sku %))
      (update :Batch/purchased-quantity #(or quantity %))
      (update :Batch/eta #(or eta %))
      (assoc :Batch/allocations #{})))

(defn- order-line-fixture
  [& {:keys [order-id sku quantity]}]
  (-> (mg/generate OrderLine)
      (update :OrderLine/order-id #(or order-id %))
      (update :OrderLine/sku #(or sku %))
      (update :OrderLine/quantity #(or quantity %))))

(defn- ->batch-and-line
  "Test helper function that creates a batch and order line from given input."
  [{:keys [sku batch-qty line-qty]}]
  [(batch-fixture {:sku sku, :quantity batch-qty})
   (order-line-fixture {:sku sku, :quantity line-qty})])

(defn- available-batch-quantities
  "Helper function that returns a list of available quantities for batches."
  [batches]
  (mapv batches/available-quantity batches))

;; Tests

(deftest batches-tests
  (testing "allocating to a batch reduces the available quantity"
    (let [;; Arrange
          batch  (batches/->batch {:reference "batch-001",
                                   :sku       "SMALL-TABLE",
                                   :quantity  20,
                                   :eta       (now)})
          line   (batches/->order-line
                   {:order-id "order-ref", :sku "SMALL-TABLE", :quantity 2})
          ;; Act
          output (batches/allocate-line batch line)]
      ;; Assert
      (is (= (batches/available-quantity output) 18))))
  (testing "can allocate if available greater than required"
    (let [;; Arrange
          [large-batch small-line]
            (->batch-and-line {:sku "ELEGANT-LAMP", :batch-qty 20, :line-qty 2})
          ;; Act
          output (batches/can-allocate large-batch small-line)]
      ;; Assert
      (is output)))
  (testing "cannot allocate if available smaller than required"
    (let [;; Arrange
          [small-batch large-line]
            (->batch-and-line {:sku "ELEGANT-LAMP", :batch-qty 2, :line-qty 20})
          ;; Act
          output (batches/can-allocate small-batch large-line)]
      ;; Assert
      (is (not output))))
  (testing "can allocate if available equal to required"
    (let [;; Arrange
          [batch line] (->batch-and-line
                         {:sku "ELEGANT-LAMP", :batch-qty 20, :line-qty 2})
          ;; Act
          output       (batches/can-allocate batch line)]
      ;; Assert
      (is output)))
  (testing "cannot allocate if skus do not match"
    (let [;; Arrange
          batch  (batch-fixture {:sku "SMALL-TABLE", :quantity 20})
          line   (order-line-fixture {:sku "LARGE-TABLE", :quantity 2})
          ;; Act
          output (batches/can-allocate batch line)]
      ;; Assert
      (is (not output))))
  (testing "can only deallocate allocated lines"
    (let [;; Arrange
          [batch line] (->batch-and-line {:sku       "DECORATIVE-TRINKET",
                                          :batch-qty 20,
                                          :line-qty  2})
          ;; Act
          output       (batches/deallocate-line batch line)]
      ;; Assert
      (is (= (batches/available-quantity output) 20))))
  (testing "allocation is idempotent"
    (let [;; Arrange
          [batch line]  (->batch-and-line
                          {:sku "ANGULAR-DESK", :batch-qty 20, :line-qty 2})
          updated-batch (batches/allocate-line batch line)
          ;; Act
          output        (batches/allocate-line updated-batch line)]
      ;; Assert
      (is (= (batches/available-quantity output) 18)))))

(deftest allocate-tests
  (testing "prefers current stock batches to shipments"
    (let [;; Arrange
          sku     "SMALL-TABLE"
          batches [(batch-fixture {:reference "batch-one",
                                   :sku       sku,
                                   :quantity  100,
                                   :eta       nil})
                   (batch-fixture {:reference "batch-two",
                                   :sku       sku,
                                   :quantity  100,
                                   :eta       (now)})]
          line    (batches/->order-line
                    {:order-id "order-ref", :sku sku, :quantity 10})
          ;; Act
          [batch-one batch-two]
            (batches/allocate-line-to-preferred-batch batches line)]
      ;; Assert
      (is (= (available-batch-quantities [batch-one batch-two]) [90 100])))))

(comment
  (clojure.test/run-tests)
  :rcf)
