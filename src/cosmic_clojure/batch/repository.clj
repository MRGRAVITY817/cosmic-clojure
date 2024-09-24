(ns cosmic-clojure.batch.repository)

(defprotocol BatchRepository
  (save [_ batch])
  (get-all [_]))

