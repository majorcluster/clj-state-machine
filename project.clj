(defproject clj-state-machine "0.1.0-SNAPSHOT"
  :description "Clojure state machine microservice"
  :dependencies [[org.clojure/clojure "1.11.1"]

                 [io.pedestal/pedestal.service "0.5.10"]
                 [io.pedestal/pedestal.route "0.5.10"]
                 [io.pedestal/pedestal.jetty "0.5.10"]

                 [ch.qos.logback/logback-classic "1.4.5" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "2.0.6"]
                 [org.slf4j/jcl-over-slf4j "2.0.6"]
                 [org.slf4j/log4j-over-slf4j "2.0.6"]

                 [org.clojure/data.json "2.4.0"]
                 [com.outpace/config "0.13.5"]
                 [prismatic/schema "1.4.1"]
                 [com.datomic/datomic-free "0.9.5697"]
                 [org.clojars.majorcluster/pedestal-api-helper "0.8.0"]
                 [org.clojars.majorcluster/datomic-helper "2.0.0"]
                 [org.clojars.majorcluster/clj-data-adapter "0.8.1"]]
  :min-lein-version "2.0.0"
  :aliases {"config"          ["run" "-m" "outpace.config.generate"]
            "diagnostics"     ["clojure-lsp" "diagnostics"]
            "format"          ["clojure-lsp" "format" "--dry"]
            "format-fix"      ["clojure-lsp" "format"]
            "clean-ns"        ["clojure-lsp" "clean-ns" "--dry"]
            "clean-ns-fix"    ["clojure-lsp" "clean-ns"]
            "lint"            ["do" ["diagnostics"]  ["format"] ["clean-ns"]]
            "lint-fix"        ["do" ["format-fix"] ["clean-ns-fix"]]}
  :resource-paths ["config", "resources"]
  :jvm-opts ["-Dresource.config.edn=app-config.edn"]
  :profiles {:dev {:aliases {"run-dev" ["trampoline" "run" "-m" "clj-state-machine.server/run-dev"]}
                   :plugins      [[com.github.clojure-lsp/lein-clojure-lsp "1.3.17"]]
                   :dependencies [[io.pedestal/pedestal.service-tools "0.5.10"]
                                  [nubank/matcher-combinators "3.8.3"]
                                  [org.clojure/test.check "1.1.1"]
                                  [prismatic/schema-generators "0.1.5"]]
                   :jvm-opts ["-Dresource.config.edn=dev-config.edn"]}
             :test {:plugins      [[com.github.clojure-lsp/lein-clojure-lsp "1.3.17"]]
                    :dependencies [[io.pedestal/pedestal.service-tools "0.5.10"]
                                   [nubank/matcher-combinators "3.8.3"]
                                   [org.clojure/test.check "1.1.1"]
                                   [prismatic/schema-generators "0.1.5"]]
                    :jvm-opts ["-Dresource.config.edn=test-config.edn"]}
             :uberjar {:aot [clj-state-machine.server]}}
  :main ^{:skip-aot true} clj-state-machine.server)
