(ns cosmic-clojure.xtdb.repositories
  (:require [cosmic-clojure.batch.repository :refer [BatchRepository]]
            [cosmic-clojure.xtdb.utils :refer [xt-datetime->inst]]
            [xtdb.api :as xt]))

(defn batch->db-model
  [batch]
  {:xt/id             (:Batch/reference batch),
   :batch/reference   (:Batch/reference batch),
   :batch/sku         (:Batch/sku batch),
   :batch/purchased-quantity (:Batch/purchased-quantity batch),
   :batch/eta         (:Batch/eta batch),
   :batch/allocations (:Batch/allocations batch)})

(defn db-model->batch
  "Converts db model to batch domain model"
  [db-model]
  {:Batch/reference   (:batch/reference db-model),
   :Batch/sku         (:batch/sku db-model),
   :Batch/purchased-quantity (:batch/purchased-quantity db-model),
   :Batch/eta         (-> (:batch/eta db-model)
                          (xt-datetime->inst)),
   :Batch/allocations (:batch/allocations db-model)})


(defn- save-batch
  [db-node batch]
  (xt/submit-tx db-node [[:put-docs :batches (batch->db-model batch)]]))

(defn- get-all-batches
  [db-node]
  (->> (xt/q db-node '(from :batches [*]))
       (map db-model->batch)))

(defn batch-repo
  [db-node]
  (reify
    BatchRepository
      (save [_ batch] (save-batch db-node batch))
      (get-all [_] (get-all-batches db-node))))
