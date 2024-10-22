(ns cosmic-clojure.batch.batches-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [cosmic-clojure.batch.batches :as batches]
   [cosmic-clojure.batch.fixtures
    :refer
    [->batch-and-line batch-fixture day-after now order-line-fixture]]))

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

(deftest allocate-to-preferred-batch-tests
  (testing "prefers current stock batches to shipments"
    (let [;; Arrange
          sku            "SMALL-TABLE"
          in-stock-batch (batch-fixture {:reference "batch-one",
                                         :sku       sku,
                                         :quantity  100,
                                         :eta       nil})
          shipping-batch (batch-fixture {:reference "batch-two",
                                         :sku       sku,
                                         :quantity  100,
                                         :eta       (now)})
          batches        [in-stock-batch shipping-batch]
          line           (batches/->order-line
                          {:order-id "order-ref", :sku sku, :quantity 10})
          ;; Act
          output         (batches/allocate-line-to-preferred-batch batches
                                                                   line)]
      ;; Assert
      (is (= output
             {:allocated (-> in-stock-batch
                             (update :Batch/allocations conj line)),
              :ignored   [shipping-batch],
              :error     nil}))))
  (testing "prefers earlier batches"
    (let [;; Arrange
          earliest-batch (batch-fixture {:reference "batch-two",
                                         :sku       "SMALL-TABLE",
                                         :quantity  100,
                                         :eta       (day-after 1)})
          second-earliest-batch (batch-fixture {:reference "batch-one",
                                                :sku       "SMALL-TABLE",
                                                :quantity  100,
                                                :eta       (day-after 2)})
          third-earliest-batch (batch-fixture {:reference "batch-three",
                                               :sku       "SMALL-TABLE",
                                               :quantity  100,
                                               :eta       (day-after 3)})
          batches        [second-earliest-batch
                          earliest-batch
                          third-earliest-batch]
          line           (batches/->order-line {:order-id "order-ref",
                                                :sku      "SMALL-TABLE",
                                                :quantity 10})
          ;; Act
          output         (batches/allocate-line-to-preferred-batch batches
                                                                   line)]
      ;; Assert
      (is (= output
             {:allocated (-> earliest-batch
                             (update :Batch/allocations conj line)),
              :ignored   [second-earliest-batch third-earliest-batch],
              :error     nil}))))
  (testing "cannot allocate if out of stock"
    (let [;; Arrange
          {:keys [allocated]} (batches/allocate-line-to-preferred-batch
                               [(batch-fixture {:reference "batch-one",
                                                :sku       "SMALL-TABLE",
                                                :quantity  10,
                                                :eta       (now)})]
                               (batches/->order-line {:order-id "order-ref",
                                                      :sku      "SMALL-TABLE",
                                                      :quantity 10}))
          ;; Act
          output (batches/allocate-line-to-preferred-batch
                  [allocated]
                  (batches/->order-line
                   {:order-id "order-ref", :sku "SMALL-TABLE", :quantity 1}))]
      ;; Assert
      (is (= output
             {:allocated nil, :ignored [allocated], :error "Out of stock"})))))

(comment
  (clojure.test/run-tests)
  :rcf)
