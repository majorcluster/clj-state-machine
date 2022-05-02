(ns clj-state-machine.controller.transition
  (:require [clj-state-machine.controller.utils :as c.utils]
            [clj-state-machine.db.config :as db.config]
            [clj-state-machine.db.transition :as db.transition]
            [datomic-helper.entity :as dh.entity]
            [pedestal-api-helper.params-helper :as p-helper])
  (:use clojure.pprint)
  (:import (java.util UUID)))

(defn get-facade
  [id]
  (let [no-id? (nil? id)
        conn (db.config/connect!)
        is-uuid? (p-helper/is-uuid id)]
    (cond no-id? (->> (dh.entity/find-all conn :transition/id)
                      (c.utils/undefine-entity-keys "transition"))
          is-uuid? (->> id
                        UUID/fromString
                        (dh.entity/find-by-id conn :transition/id)
                        (c.utils/undefine-entity-keys "transition"))
          :else nil)))

(defn upsert-facade
  [workflow-id transition]
  (let [workflow-id (cond (string? workflow-id) (UUID/fromString workflow-id)
                          :else workflow-id)
        id-from-upsert (db.transition/upsert! workflow-id (c.utils/redefine-entity-keys "transition" transition))]
    {:status 200
     :headers c.utils/headers
     :body {:message ""
            :payload {:id id-from-upsert}}}))

(defn delete-facade
  [_ id]
  (db.transition/delete! id)
  {:status 204})
