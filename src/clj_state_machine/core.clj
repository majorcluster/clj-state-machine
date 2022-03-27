(ns clj-state-machine.core
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [clj-state-machine.routes.core :as r.core]
            [clj-state-machine.db.config :as db.config]
            [schema.core :as s]))

(defn create-server []
  (http/create-server
    {::http/routes r.core/all-routes
     ::http/type   :jetty
     ::http/port   8890}))

(defn start []
  (s/set-fn-validation! true)
  (db.config/start-db)
  (http/start (create-server)))

(defn -main
  [& args]
  (start))