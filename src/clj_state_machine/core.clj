(ns clj-state-machine.core
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [clj-state-machine.routes.core :as r.core]
            [clj-state-machine.db.config :as db.config]
            [environ.core :refer [env]]
            [schema.core :as s]))

(defn service-map
  []
  {::http/routes r.core/all-routes
   ::http/type   :jetty
   ::http/port   (Integer/parseInt (env :port))})

(defn create-server []
  (http/create-server
    (service-map)))

(defn start []
  (s/set-fn-validation! true)
  (db.config/start-db)
  (cond (= (env :name) "test") (-> (service-map)
                                   (http/create-servlet)
                                   :io.pedestal.http/service-fn)
        :else (http/start (create-server))))

(defn -main
  [& args]
  (start))