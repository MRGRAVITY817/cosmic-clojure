(ns cosmic-clojure.system
  (:require [integrant.core :as ig]
            [ring.adapter.jetty :refer [run-jetty]]
            [cosmic-clojure.handler :as handler]
            [cosmic-clojure.xtdb.repositories :refer [xtdb-repos]]
            [cosmic-clojure.xtdb.connection :refer [xtdb-client]])
  (:gen-class))

;; Initialize the Jetty adapter
(defmethod ig/init-key :adapter/jetty
  [_ {:keys [handler], :as opts}]
  (run-jetty handler
             (-> opts
                 (dissoc handler)
                 (assoc :join? false))))

;; Initialize the handler
(defmethod ig/init-key :handler/run-app
  [_ {:keys [repos]}]
  (handler/app repos))

;; Initialize the repositories
(defmethod ig/init-key :app/repos
  [_ {:keys [db]}]
  (xtdb-repos db))

;; Initialize the database connection
(defmethod ig/init-key :db/client
  [_ {:keys [type url]}]
  (xtdb-client {:type type, :url url}))

;; Close Jetty server
(defmethod ig/halt-key! :adapter/jetty
  [_ server]
  (.stop server))

(def config
  {:adapter/jetty   {:handler (ig/ref :handler/run-app), :port 4000},
   :handler/run-app {:repos (ig/ref :app/repos)},
   :app/repos       {:db (ig/ref :db/client)},
   :db/client       {:type :in-process}})

(defn -main [] (ig/init config))

