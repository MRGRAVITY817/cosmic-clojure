(ns cosmic-clojure.batch.usecase
  (:require
   [cosmic-clojure.batch.batches :as batches]
   [cosmic-clojure.batch.repository :as batch-repo]))

(defn allocate-order-line-to-batch [batch-repo order-line]
  (let [order-line (batches/->order-line order-line)
        batches    (batch-repo/get-all batch-repo)
        output     (batches/allocate-line-to-preferred-batch batches order-line)]
    {:batch-reference (-> output
                          (:allocated)
                          (batches/reference))}))
