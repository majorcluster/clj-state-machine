(ns clj-state-machine.ports.datomic.core
  (:require [clj-state-machine.ports.datomic.schema :refer [specs]]
            [datomic.api :as d]
            [outpace.config :refer [defconfig]]))


(defconfig db-host)
(defconfig db-name)

(defonce db-uri (str db-host "/" db-name))

(defn create-db! []
  (d/create-database db-uri))

(defn connect! []
  (d/connect db-uri))

(defn create-schema!
  [conn]
  (d/transact conn specs))

(defn erase-db!
  "test use only!!!"
  []
  (println "ERASING DB!!!!!!!")
  (d/delete-database db-uri))

(defn start-db
  []
  (create-db!)
  (let [conn (connect!)]
    (create-schema! conn)))
