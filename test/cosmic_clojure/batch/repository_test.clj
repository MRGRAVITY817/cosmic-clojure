(ns cosmic-clojure.batch.repository-test
  (:require [clojure.test :refer [deftest is testing]]
            [cosmic-clojure.batch.batches :as batches]
            [cosmic-clojure.batch.repository :as batch-repo]
            [cosmic-clojure.xtdb.connection :refer [db-node]]
            [cosmic-clojure.xtdb.repositories :as xtdb]))

(deftest repository-test
  (testing "can save a batch"
    (let [;; Arrange
          repo    (xtdb/batch-repo db-node)
          batch   (batches/->batch {:reference "batch-001",
                                    :sku       "SMALL-TABLE",
                                    :quantity  10,
                                    :eta       nil})
          _result (batch-repo/save repo batch)
          ;; Act
          output  (batch-repo/get-all repo)]
      ;; Assert
      (is (= output
             [{:xt/id             "batch-001",
               :batch/reference   "batch-001",
               :batch/sku         "SMALL-TABLE",
               :batch/purchased-quantity 10,
               :batch/eta         nil,
               :batch/allocations #{}}])))))

(comment
  (clojure.test/run-tests)
  :rcf)
