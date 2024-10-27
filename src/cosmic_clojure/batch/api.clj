(ns cosmic-clojure.batch.api
  (:require
   [cosmic-clojure.batch.usecase :as usecase]
   [ring.util.response :as resp]))

(defn allocate-handler
  "A handler for allocating an order line to a batch."
  [{:keys [repos body] :as _req}]
  (let [batch-repo (:batch-repo repos)
        order-line (:order-line body)
        batch-ref  (usecase/allocate-order-line-to-batch batch-repo order-line)]
    (resp/response batch-ref)))
