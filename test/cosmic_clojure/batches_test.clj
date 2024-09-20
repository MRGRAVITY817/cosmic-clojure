(ns cosmic-clojure.batches-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [cosmic-clojure.batches :as batches]))

;; Test helpers

(defn- now [] (new java.util.Date))

(defn- ->batch-and-line
  "Test helper function that creates a batch and order line from given input."
  [{:keys [sku batch-qty line-qty]}]
  [#:Batch{:reference "batch-001"
           :sku       sku
           :quantity  batch-qty
           :eta       (now)}
   #:OrderLine{:order-id "order-123"
               :sku      sku
               :quantity line-qty}])

;; Tests

(deftest batches-tests
  (testing "allocating to a batch reduces the available quantity"
    (let [;; Arrange 
          batch #:Batch{:reference "batch-001"
                        :sku       "SMALL-TABLE"
                        :quantity  20
                        :eta       (now)}
          line  #:OrderLine{:order-id "order-ref"
                            :sku      "SMALL-TABLE"
                            :quantity 2}
         ;; Act
          output (batches/allocate-line batch line)]
     ;; Assert
      (is (= (batches/available-quantity output) 18))))
  (testing "can allocate if available greater than required"
    (let [;; Arrange
          [large-batch small-line] (->batch-and-line {:sku      "ELEGANT-LAMP"
                                                      :batch-qty 20
                                                      :line-qty  2})
          ;; Act
          output                   (batches/can-allocate large-batch small-line)]
      ;; Assert
      (is output)))
  (testing "cannot allocate if available smaller than required"
    (let [;; Arrange
          [small-batch large-line] (->batch-and-line {:sku      "ELEGANT-LAMP"
                                                      :batch-qty 2
                                                      :line-qty  20})
          ;; Act
          output                   (batches/can-allocate small-batch large-line)]
      ;; Assert
      (is (not output))))
  (testing "can allocate if available equal to required"
    (let [;; Arrange
          [batch line] (->batch-and-line {:sku      "ELEGANT-LAMP"
                                          :batch-qty 20
                                          :line-qty  2})
          ;; Act
          output       (batches/can-allocate batch line)]
      ;; Assert
      (is output)))
  (testing "cannot allocate if skus do not match"
    (let [;; Arrange
          batch #:Batch{:reference "batch-001"
                        :sku       "SMALL-TABLE"
                        :quantity  20
                        :eta       (now)}
          line  #:OrderLine{:order-id "order-ref"
                            :sku      "LARGE-TABLE"
                            :quantity 2}
          ;; Act
          output       (batches/can-allocate batch line)]
      ;; Assert
      (is (not output)))))

(comment
  (clojure.test/run-tests)
  :rcf)
