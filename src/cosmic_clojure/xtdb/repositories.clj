(ns cosmic-clojure.xtdb.repositories
  (:require [cosmic-clojure.batch.repository :refer [BatchRepository]]
            [cosmic-clojure.xtdb.utils :refer [xt-datetime->inst]]
            [xtdb.api :as xt]
            [clojure.core :as c]))

(defn order-line->db-model [order-line]
  {:xt/id               (:OrderLine/order-id order-line),
   :order-line/order-id (:OrderLine/order-id order-line),
   :order-line/sku      (:OrderLine/sku order-line),
   :order-line/quantity (:OrderLine/quantity order-line)})

(defn batch->db-model
  [batch]
  {:xt/id             (:Batch/reference batch),
   :batch/reference   (:Batch/reference batch),
   :batch/sku         (:Batch/sku batch),
   :batch/purchased-quantity (:Batch/purchased-quantity batch),
   :batch/eta         (:Batch/eta batch),
   :batch/allocations (->> (:Batch/allocations batch)
                           (map order-line->db-model)
                           (set))})

(defn db-model->order-line [db-model]
  {:OrderLine/order-id (:order-line/order-id db-model),
   :OrderLine/sku      (:order-line/sku db-model),
   :OrderLine/quantity (:order-line/quantity db-model)})

(defn db-model->batch
  "Converts db model to batch domain model"
  [db-model]
  {:Batch/reference   (:batch/reference db-model),
   :Batch/sku         (:batch/sku db-model),
   :Batch/purchased-quantity (:batch/purchased-quantity db-model),
   :Batch/eta         (-> (:batch/eta db-model)
                          (xt-datetime->inst)),
   :Batch/allocations (->> (:batch/allocations db-model)
                           (map db-model->order-line)
                           (set))})

(defn- save-batch!
  [db-node batch]
  (xt/submit-tx db-node [[:put-docs :batches (batch->db-model batch)]]))
(defn- get-all-batches
  [db-node]
  (->> (xt/q db-node '(from :batches [*]))
       (map db-model->batch)))

(defn- get-batch-by-reference [db-node reference]
  (->> (xt/q db-node
             '(-> (from :batches [{:batch/reference $reference} *])
                  (limit 1))
             {:args {:reference reference}})
       (map db-model->batch)
       (first)))

(defn batch-repo
  [db-node]
  (reify
    BatchRepository
    (save! [_ batch] (save-batch! db-node batch))
    (get-all [_] (get-all-batches db-node))
    (get-by-reference [_ reference] (get-batch-by-reference db-node reference))))

(defn xtdb-repos
  "Provides a map of repositories to be used in the application"
  [db-node]
  {:batch-repo (batch-repo db-node)})

(comment
  :rcf)
