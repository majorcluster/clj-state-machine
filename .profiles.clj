;profiles example file, copy it and remove the "." from the file
{:dev {:env {:name "dev"
             :database-url "datomic:dev://localhost:4334/clj-state-machine-local"
             :port 8890}}
 :test {:env {:name "test"
              :database-url "datomic:dev://localhost:4334/clj-state-machine-test-local"
              :port 8005}}}