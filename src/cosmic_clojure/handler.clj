(ns cosmic-clojure.handler
  (:require [ring.util.response :as resp]
            [reitit.ring :as ring]
            [reitit.ring.middleware.parameters :as parameters]
            [ring.middleware.json :refer [wrap-json-body]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [cosmic-clojure.batch.api :as batch-api]))

(def repo-middleware
  "Put implementation of repositories in the request map."
  {:name    ::repos
   :compile (fn [{:keys [repos]} _]
              (fn [handler]
                (fn [req]
                  (handler (assoc req :repos repos)))))})

(def ^:private greeting-message
  "Hello ^^")

(defn hello-world [_req]
  (resp/response greeting-message))

(defn app [repos]
  (ring/ring-handler
   (ring/router
    [["/" {:handler #'hello-world}]
     ["/allocate" {:post {:handler #'batch-api/allocate-handler}}]]

    {:data {:repos      repos
            :middleware [parameters/parameters-middleware
                         wrap-keyword-params
                         #(wrap-json-body % {:keywords? true})
                         repo-middleware]}})

   (ring/routes
    (ring/redirect-trailing-slash-handler)
    (ring/create-resource-handler
     {:path "/"})
    (ring/create-default-handler
     {:not-found          (constantly {:status 404 :body "Not found"})
      :method-not-allowed (constantly {:status 405 :body "Method not allowed"})
      :not-acceptable     (constantly {:status 406 :body "Not acceptable"})}))))

