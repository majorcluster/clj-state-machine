(ns clj-state-machine.adapters.commons
  (:require [pedestal-api-helper.params-helper :as p-helper]
            [schema.core :as s])
  (:import [java.util UUID]))

(s/defn str->uuid :- (s/maybe s/Uuid)
  [uuid :- (s/maybe s/Str)]
  (cond (p-helper/is-uuid uuid) (UUID/fromString uuid)
        (uuid? uuid) uuid
        :else nil))
