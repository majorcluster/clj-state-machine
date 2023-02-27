(ns clj-state-machine.wire.http-in.commons
  (:require [schema.core :as s]))

(s/defn Response
  [skeleton :- s/Any]
  {:status s/Num
   (s/optional-key :headers) (s/maybe s/Any)
   (s/optional-key :body) (s/maybe {(s/optional-key :message) (s/maybe s/Str)
                                    (s/optional-key :validation-messages) [{(s/optional-key :field) (s/maybe s/Str)
                                                                            (s/optional-key :message) (s/maybe s/Str)}]
                                    (s/optional-key :payload) (s/maybe skeleton)})})
