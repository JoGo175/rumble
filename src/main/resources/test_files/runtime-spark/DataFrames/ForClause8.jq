(:JIQS: ShouldRun; Output="({ "left" : "Latvian", "right" : "Latvian" }, { "left" : "Latvian", "right" : "Russian" }, { "left" : "Latvian", "right" : "Czech" }, { "left" : "Latvian", "right" : "Greek" }, { "left" : "Latvian", "right" : "Serbian" }, { "left" : "Russian", "right" : "Latvian" }, { "left" : "Russian", "right" : "Russian" }, { "left" : "Russian", "right" : "Czech" }, { "left" : "Russian", "right" : "Greek" }, { "left" : "Russian", "right" : "Serbian" }, { "left" : "Czech", "right" : "Latvian" }, { "left" : "Czech", "right" : "Russian" }, { "left" : "Czech", "right" : "Czech" }, { "left" : "Czech", "right" : "Greek" }, { "left" : "Czech", "right" : "Serbian" }, { "left" : "Greek", "right" : "Latvian" }, { "left" : "Greek", "right" : "Russian" }, { "left" : "Greek", "right" : "Czech" }, { "left" : "Greek", "right" : "Greek" }, { "left" : "Greek", "right" : "Serbian" }, { "left" : "Serbian", "right" : "Latvian" }, { "left" : "Serbian", "right" : "Russian" }, { "left" : "Serbian", "right" : "Czech" }, { "left" : "Serbian", "right" : "Greek" }, { "left" : "Serbian", "right" : "Serbian" })" :)
for $i in json-file("./src/main/resources/queries/conf-ex.json")
for $j in json-file("./src/main/resources/queries/conf-ex.json")
return { left: $i.guess, right: $j.guess }

(: Cartesion product with two Spark-enabled for clauses :)
