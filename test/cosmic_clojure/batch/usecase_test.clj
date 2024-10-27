
(ns cosmic-clojure.batch.usecase-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [cosmic-clojure.batch.batches :as batches]
   [cosmic-clojure.batch.repository :as batch-repo]
   [cosmic-clojure.batch.test-utils :refer [->test-batch-repo]]
   [cosmic-clojure.batch.fixtures :as fixtures]
   [cosmic-clojure.batch.usecase :as sut]))

(deftest allocate-order-line-to-batch-test
  (testing "allocates an order line to a batch"
    (let [;; Arrange
          sku        "SMALL-TABLE"
          batch-repo (->test-batch-repo)
          order-line {:order-id "order-1"
                      :sku      sku
                      :quantity 1}
          batch-1    (batches/->batch {:reference "batch-001",
                                       :sku       sku,
                                       :quantity  10,
                                       :eta       (fixtures/now)})
          _          (batch-repo/save batch-repo batch-1)
          ;; Act
          output (sut/allocate-order-line-to-batch
                  batch-repo order-line)]
      ;; Assert
      (is (= output {:batch-reference "batch-001"})))))
