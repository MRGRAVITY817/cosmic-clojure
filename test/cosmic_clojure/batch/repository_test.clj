(ns cosmic-clojure.batch.repository-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [cosmic-clojure.batch.batches :as batches]
   [cosmic-clojure.batch.fixtures :refer [now]]
   [cosmic-clojure.batch.repository :as batch-repo]
   [cosmic-clojure.xtdb.connection :refer [db-node]] [cosmic-clojure.xtdb.repositories :as xtdb]))

(deftest repository-test
  (testing "can save and retrieve a batch"
    (let [;; Arrange
          repo    (xtdb/batch-repo db-node)
          eta     (now)
          batch   (batches/->batch {:reference "batch-001",
                                    :sku       "SMALL-TABLE",
                                    :quantity  10,
                                    :eta       eta})
          _result (batch-repo/save repo batch)
          ;; Act
          output  (batch-repo/get-all repo)]
      ;; Assert
      (is (= output
             [{:Batch/reference   "batch-001",
               :Batch/sku         "SMALL-TABLE",
               :Batch/purchased-quantity 10,
               :Batch/eta         eta,
               :Batch/allocations #{}}]))))
  (testing "can save and retreive a batch with allocations"
    (let [;; Arrange
          repo    (xtdb/batch-repo db-node)
          eta     (now)
          batch   (batches/allocate-line
                   (batches/->batch {:reference "batch-001",
                                     :sku       "SMALL-TABLE",
                                     :quantity  10,
                                     :eta       eta})
                   (batches/->order-line {:order-id "order-001",
                                          :sku      "SMALL-TABLE",
                                          :quantity 1}))
          _result (batch-repo/save repo batch)
          ;; Act
          output  (batch-repo/get-all repo)]
      ;; Assert
      (is (= output
             [{:Batch/reference   "batch-001",
               :Batch/sku         "SMALL-TABLE",
               :Batch/purchased-quantity 10,
               :Batch/eta         eta,
               :Batch/allocations #{(batches/->order-line {:order-id "order-001",
                                                           :sku      "SMALL-TABLE",
                                                           :quantity 1})}}]))))
  (testing "save and get batch by reference"
    (let [;; Arrange
          repo    (xtdb/batch-repo db-node)
          eta     (now)
          batch   (batches/allocate-line
                   (batches/->batch {:reference "batch-001",
                                     :sku       "SMALL-TABLE",
                                     :quantity  10,
                                     :eta       eta})
                   (batches/->order-line {:order-id "order-001",
                                          :sku      "SMALL-TABLE",
                                          :quantity 1}))
          _result (batch-repo/save repo batch)
          ;; Act
          output  (batch-repo/get-by-reference repo "batch-001")]
      ;; Assert
      (is (= output
             {:Batch/reference   "batch-001",
              :Batch/sku         "SMALL-TABLE",
              :Batch/purchased-quantity 10,
              :Batch/eta         eta,
              :Batch/allocations #{(batches/->order-line {:order-id "order-001",
                                                          :sku      "SMALL-TABLE",
                                                          :quantity 1})}})))))

(comment
  (clojure.test/run-tests)
  :rcf)

