(ns clj-state-machine.db.config
  (:require [datomic.api :as d]
            [environ.core :refer [env]]
            [clj-state-machine.model.status :as model.status]
            [clj-state-machine.model.workflow :as model.workflow]
            [datomic-helper.schema-transform :as dh.s-transform])
  (:use clojure.pprint))

(def db-uri
  (env :database-url))

(defn connect! []
  (d/create-database db-uri)
  (d/connect db-uri))

(defn get-schema []
  (let [datomic-schemas (dh.s-transform/schemas-to-datomic [(model.status/StatusDef)
                                                      (model.workflow/WorkflowDef)])]
    (pprint datomic-schemas)
    datomic-schemas))

(defn create-schema!
  [conn]
  (d/transact conn (get-schema)))

(defn erase-db!
  "test use only!!!"
  []
  (println "ERASING DB!!!!!!!")
  (d/delete-database db-uri))

(defn reset-db!
  "test use only!!!"
  []
  (erase-db!)
  (let [conn (connect!)]
    (create-schema! conn)
    conn))

(defn start-db
  []
  (let [conn (connect!)]
    (create-schema! conn)))