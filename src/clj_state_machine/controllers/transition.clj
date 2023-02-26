(ns clj-state-machine.controllers.transition
  (:require [clj-state-machine.controllers.utils :as controllers.utils]
            [clj-state-machine.models.transition :as models.transition]
            [clj-state-machine.ports.datomic.transition :as datomic.transition]
            [schema.core :as s])
  (:import (java.util UUID)))

(s/defn get-facade :- (s/cond-pre [models.transition/TransitionDef] models.transition/TransitionDef)
  [id :- (s/maybe s/Uuid)]
  (let [no-id? (nil? id)]
    (cond no-id? (datomic.transition/find-all)
          :else (datomic.transition/find-one id))))

(defn upsert-facade
  [workflow-id transition]
  (let [workflow-id (cond (string? workflow-id) (UUID/fromString workflow-id)
                          :else workflow-id)
        id-from-upsert (datomic.transition/upsert! workflow-id (controllers.utils/redefine-entity-keys "transition" transition))]
    {:status  200
     :headers controllers.utils/headers
     :body    {:message ""
            :payload {:id id-from-upsert}}}))

(defn delete-facade
  [_ id]
  (datomic.transition/delete! id)
  {:status 204})
