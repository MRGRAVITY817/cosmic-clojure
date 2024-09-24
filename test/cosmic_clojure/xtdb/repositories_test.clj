(ns cosmic-clojure.xtdb.repositories-test
  (:require [clojure.test :refer [deftest is]]
            [cosmic-clojure.batch.batches :as batches]
            [cosmic-clojure.xtdb.repositories :as sut]))

(deftest batch->db-model-test
  (let [;; Arrange
        batch  (batches/->batch {:reference "batch-001",
                                 :sku       "SMALL-TABLE",
                                 :quantity  10,
                                 :eta       nil})
        ;; Act
        output (sut/batch->db-model batch)]
    ;; Assert
    (is (= output
           {:xt/id             "batch-001",
            :batch/reference   "batch-001",
            :batch/sku         "SMALL-TABLE",
            :batch/purchased-quantity 10,
            :batch/eta         nil,
            :batch/allocations #{}}))))

(comment
  (clojure.test/run-tests)
  :rcf)
