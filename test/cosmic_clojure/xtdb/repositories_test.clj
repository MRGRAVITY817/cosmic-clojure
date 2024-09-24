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

(= []
   [{:xt/id             "batch-001",
     :batch/reference   "batch-001",
     :batch/sku         "SMALL-TABLE",
     :batch/purchased-quantity 10,
     :batch/eta         #inst "2024-09-24T14:08:28.215-00:00",
     :batch/allocations #{}}])
(deftest db-model->batch
  (let [;; Arrange
        db-model {:xt/id             "batch-001",
                  :batch/reference   "batch-001",
                  :batch/sku         "SMALL-TABLE",
                  :batch/purchased-quantity 10,
                  :batch/allocations #{},
                  :batch/eta         #xt.time/zoned-date-time
                                      "2024-09-24T14:08:28.215Z[UTC]"}
        ;; Act
        output   (sut/db-model->batch db-model)]
    ;; Assert
    (is (= output
           {:Batch/reference   "batch-001",
            :Batch/sku         "SMALL-TABLE",
            :Batch/purchased-quantity 10,
            :Batch/eta         #inst "2024-09-24T14:08:28.215-00:00",
            :Batch/allocations #{}}))))


(comment
  (clojure.test/run-tests)
  :rcf)
