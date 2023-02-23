(ns clj-state-machine.controllers.workflow
  (:require [clj-state-machine.controllers.utils :as c.utils]
            [clj-state-machine.ports.datomic.core :as datomic.core]
            [clj-state-machine.ports.datomic.workflow :as datomic.workflow]
            [datomic-helper.entity :as dh.entity]
            [pedestal-api-helper.params-helper :as p-helper])
  (:import (java.util UUID)))

(defn get-facade
  [id]
  (let [no-id? (nil? id)
        conn (datomic.core/connect!)
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
  (let [id-from-upsert (datomic.workflow/upsert! (c.utils/redefine-entity-keys "workflow" workflow))]
    {:status 200
     :headers c.utils/headers
     :body {:message ""
            :payload {:id id-from-upsert}}}))

(defn delete-facade
  [_ id]
  (datomic.workflow/delete! id)
  {:status 204})
