(ns clj-state-machine.controllers.transition
  (:require [clj-state-machine.models.transition :as models.transition]
            [clj-state-machine.ports.datomic.transition :as datomic.transition]
            [schema.core :as s]))

(s/defn get-facade :- (s/cond-pre [models.transition/TransitionDef] models.transition/TransitionDef)
  [id :- (s/maybe s/Uuid)
   workflow-id :- (s/maybe s/Uuid)]
  (cond id (datomic.transition/find-one id)
        :else (datomic.transition/find-all workflow-id)))

(s/defn get-by-status-from-facade :- [models.transition/TransitionDef]
  [workflow-id :- (s/maybe s/Uuid)
   status-from :- (s/maybe s/Uuid)]
  (cond status-from (datomic.transition/find-all-by-workflow-and-status-from workflow-id status-from)
        :else  (datomic.transition/find-all-initial workflow-id)))

(s/defn upsert-facade :- s/Uuid
  [transition :- models.transition/TransitionInputDef
   workflow-id :- s/Uuid]
  (datomic.transition/upsert! workflow-id transition))

(s/defn patch-facade :- s/Uuid
  [transition :- models.transition/TransitionLooseInputDef
   workflow-id :- s/Uuid]
  (datomic.transition/upsert! workflow-id transition))

(s/defn delete-facade
  [_ :- s/Keyword
   id :- s/Uuid]
  (datomic.transition/delete! id))
