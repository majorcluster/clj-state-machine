(ns clj-state-machine.controllers.transition
  (:require [clj-state-machine.controllers.utils :as c.utils]
            [clj-state-machine.ports.datomic.core :as datomic.core]
            [clj-state-machine.ports.datomic.transition :as datomic.transition]
            [datomic-helper.entity :as dh.entity]
            [pedestal-api-helper.params-helper :as p-helper])
  (:import (java.util UUID)))

(defn get-facade
  [id]
  (let [no-id? (nil? id)
        conn (datomic.core/connect!)
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
        id-from-upsert (datomic.transition/upsert! workflow-id (c.utils/redefine-entity-keys "transition" transition))]
    {:status 200
     :headers c.utils/headers
     :body {:message ""
            :payload {:id id-from-upsert}}}))

(defn delete-facade
  [_ id]
  (datomic.transition/delete! id)
  {:status 204})
