(ns cosmic-clojure.xtdb.repositories-test
  (:require [clojure.test :refer [deftest is]]
            [cosmic-clojure.batch.batches :as batches]
            [cosmic-clojure.xtdb.repositories :as sut]))

(deftest batch->db-model-test
  (let [;; Arrange
        batch  (batches/allocate-line
                (batches/->batch {:reference "batch-001",
                                  :sku       "SMALL-TABLE",
                                  :quantity  10,
                                  :eta       nil})
                (batches/->order-line {:order-id "order-001",
                                       :sku      "SMALL-TABLE",
                                       :quantity 1}))
        ;; Act
        output (sut/batch->db-model batch)]
    ;; Assert
    (is (= output
           {:xt/id             "batch-001",
            :batch/reference   "batch-001",
            :batch/sku         "SMALL-TABLE",
            :batch/purchased-quantity 10,
            :batch/eta         nil,
            :batch/allocations #{{:xt/id             "order-001",
                                  :order-line/order-id "order-001",
                                  :order-line/sku      "SMALL-TABLE",
                                  :order-line/quantity 1}}}))))

(deftest db-model->batch-test
  (let [;; Arrange
        db-model {:xt/id             "batch-001",
                  :batch/reference   "batch-001",
                  :batch/sku         "SMALL-TABLE",
                  :batch/purchased-quantity 10,
                  :batch/allocations #{{:order-line/order-id "order-001",
                                        :order-line/sku      "SMALL-TABLE",
                                        :order-line/quantity 1}},
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
            :Batch/allocations #{{:OrderLine/order-id "order-001",
                                  :OrderLine/sku      "SMALL-TABLE",
                                  :OrderLine/quantity 1}}}))))

(deftest order-line->db-model-test
  (let [;; Arrange
        order-line {:OrderLine/order-id "order-001",
                    :OrderLine/sku      "SMALL-TABLE",
                    :OrderLine/quantity 1}
        ;; Act
        output (sut/order-line->db-model order-line)]
    ;; Assert
    (is (= output
           {:xt/id               "order-001",
            :order-line/order-id "order-001",
            :order-line/sku      "SMALL-TABLE",
            :order-line/quantity 1}))))

(deftest db-model->order-line-test
  (let [;; Arrange
        db-model {:xt/id               "order-001",
                  :order-line/order-id "order-001",
                  :order-line/sku      "SMALL-TABLE",
                  :order-line/quantity 1}
        ;; Act
        output   (sut/db-model->order-line db-model)]
    ;; Assert
    (is (= output
           {:OrderLine/order-id "order-001",
            :OrderLine/sku      "SMALL-TABLE",
            :OrderLine/quantity 1}))))

(comment
  (clojure.test/run-tests)
  :rcf)

