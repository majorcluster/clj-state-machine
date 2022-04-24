(ns clj-state-machine.controller.workflow
  (:require [clj-state-machine.controller.utils :as c.utils]
            [clj-state-machine.db.config :as db.config]
            [clj-state-machine.db.workflow :as db.workflow]
            [pedestal-api-helper.params-helper :as p-helper]
            [datomic-helper.entity :as dh.entity])
  (:use clojure.pprint)
  (:import (java.util UUID)))

(defn get-facade
  [id]
  (let [no-id? (nil? id)
        conn (db.config/connect!)
        is-uuid? (p-helper/is-uuid id)]
    (cond no-id? (->> (dh.entity/find-all conn :workflow/id)
                      (c.utils/undefine-entity-keys "workflow"))
          is-uuid? (->> id
                        UUID/fromString
                        (dh.entity/find-by-id conn :workflow/id)
                        (c.utils/undefine-entity-keys "workflow"))
          :else nil)))

(defn upsert-facade
  [workflow]
  (let [id-from-upsert (db.workflow/upsert! (c.utils/redefine-entity-keys "workflow" workflow))]
    {:status 200
     :headers c.utils/headers
     :body {:message ""
            :payload {:id id-from-upsert}}}))

(defn delete-facade
  [_ id]
  (db.workflow/delete! id)
  {:status 204})
