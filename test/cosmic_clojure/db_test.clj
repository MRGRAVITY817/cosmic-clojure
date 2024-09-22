(ns cosmic-clojure.db-test
  (:require [clojure.test :refer [deftest is testing]]
            [cosmic-clojure.db :refer [db-node]]
            [cosmic-clojure.fixtures.batch-fixtures :refer [order-line-fixture]]
            [xtdb.api :as xt]))

(defn- order-line->db-model
  "Test helper function that converts order line to db model."
  [{:keys [OrderLine/order-id OrderLine/sku OrderLine/quantity]}]
  {:xt/id (str order-id "-" sku),
   :order-line/order-id order-id,
   :order-line/sku sku,
   :order-line/quantity quantity})

;; These tests verify if db works as expected
;; Can be thrown away if they are not needed

(deftest db-connection-test
  (testing "db is connected"
    (let [;; Arrange + Act
          output (xt/status db-node)]
      ;; Assert
      (is (contains? output :latest-completed-tx))
      (is (contains? output :latest-submitted-tx))))
  (testing "insert and retrieve order-lines"
    (let [;; Arrange
          order-lines [(order-line-fixture {:order-id "order-001",
                                            :sku      "SMALL-TABLE",
                                            :quantity 10})
                       (order-line-fixture {:order-id "order-002",
                                            :sku      "LARGE-TABLE",
                                            :quantity 20})
                       (order-line-fixture {:order-id "order-003",
                                            :sku      "EX-LARGE-TABLE",
                                            :quantity 30})]
          _ (xt/submit-tx
              db-node
              (for [order-line order-lines]
                [:put-docs :order-lines (order-line->db-model order-line)]))
          ;; Act
          output      (xt/q db-node
                            '(from
                              :order-lines
                              [xt/id
                               order-line/order-id
                               order-line/sku
                               order-line/quantity]))]
      ;; Assert
      (is (= output
             [{:order-line/sku "LARGE-TABLE",
               :order-line/quantity 20,
               :order-line/order-id "order-002",
               :xt/id "order-002-LARGE-TABLE"}
              {:order-line/sku "EX-LARGE-TABLE",
               :order-line/quantity 30,
               :order-line/order-id "order-003",
               :xt/id "order-003-EX-LARGE-TABLE"}
              {:order-line/sku "SMALL-TABLE",
               :order-line/quantity 10,
               :order-line/order-id "order-001",
               :xt/id "order-001-SMALL-TABLE"}])))))


(comment
  (clojure.test/run-tests)
  :rcf)
