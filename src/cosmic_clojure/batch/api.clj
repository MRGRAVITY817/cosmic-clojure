(ns cosmic-clojure.batch.api
  (:require
   [cosmic-clojure.batch.batches :as batches]
   [ring.util.response :as resp]))

(defn allocate-handler
  "A handler for allocating a batch to an order line."
  [{:keys [repos body] :as _req}]
  (let [batch-ref "ref-1"]
    (resp/response {:batch-ref batch-ref})))
