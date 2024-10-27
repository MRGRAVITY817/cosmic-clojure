(ns cosmic-clojure.batch.api-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [cosmic-clojure.batch.fixtures :refer [batch-fixture day-after now]]
   [cosmic-clojure.batch.interface :as batch]
   [cosmic-clojure.batch.test-utils :refer [->test-batch-repo]]
   [cosmic-clojure.test-utils :refer [api-handler]]
   [ring.mock.request :as mock]))

(deftest batch-api-test
  (testing "api returns allocation"
    (let [sku         "SMALL-TABLE"
          other-sku   "LARGE-TABLE"
          early-batch (batch-fixture {:reference "ref-1"
                                      :sku        sku
                                      :quantity   20
                                      :eta        (now)})
          later-batch (batch-fixture {:reference "ref-2"
                                      :sku        sku
                                      :quantity   20
                                      :eta        (day-after 2)})
          other-batch (batch-fixture {:reference "ref-3"
                                      :sku        other-sku
                                      :quantity   20
                                      :eta        nil})
          repo        (->test-batch-repo)
          _           (batch/save! repo early-batch)
          _           (batch/save! repo later-batch)
          _           (batch/save! repo other-batch)
          input       {:order-line {:order-id "order-1"
                                    :sku      sku
                                    :quantity 1}}
          handler     (api-handler {:batch-repo repo})
          output      (handler
                       (-> (mock/request :post "/allocate")
                           (mock/json-body input)))]
      (is (= {:headers {},
              :status  200
              :body    {:batch-reference "ref-1"}}
             output)))))
