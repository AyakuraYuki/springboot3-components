CURRENT_TARGET := $(filter-out $@,$(MAKECMDGOALS))
ifneq ($(CURRENT_TARGET),clean)
	ifndef VERSION
		$(error VERSION is not set)
	endif
else
	ifndef VERSION
		VERSION=1.0.0
	endif
endif

package:
	mvn -B -T 1C clean package -DskipTests -Dmaven.test.skip=true -Dautoconfig.skip -Drevision=$(VERSION)

deploy:
	mvn -B -T 1C clean package org.apache.maven.plugins:maven-deploy-plugin:2.8:deploy -DskipTests -Dmaven.test.skip=true -Dautoconfig.skip -Drevision=$(VERSION)

clean:
	mvn clean -Drevision=$(VERSION)
