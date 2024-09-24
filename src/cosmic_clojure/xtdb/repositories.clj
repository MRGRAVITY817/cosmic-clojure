(ns cosmic-clojure.xtdb.repositories
  (:require [cosmic-clojure.batch.repository :refer [BatchRepository]]
            [xtdb.api :as xt]))

(defn batch->db-model
  [batch]
  {:batch/reference   (:Batch/reference batch),
   :batch/sku         (:Batch/sku batch),
   :batch/purchased-quantity (:Batch/purchased-quantity batch),
   :batch/eta         (:Batch/eta batch),
   :batch/allocations (:Batch/allocations batch)})

(defn- save-batch
  [db-node batch]
  (xt/submit-tx db-node [[:put-docs :batches (batch->db-model batch)]]))

(defn- get-all-batches [db-node])

(defn batch-repo
  [db-node]
  (reify
    BatchRepository
      (save [_ batch] (save-batch db-node batch))
      (get-all [_] (get-all-batches db-node))))
