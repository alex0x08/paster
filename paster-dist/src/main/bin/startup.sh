#!/usr/bin/env bash
#
# Copyright © 2011 Alex Chernyshev (alex3.145@gmail.com)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
home=$(readlink -f "$(dirname $0)")
nohup /usr/bin/env java -DappName=pasterApp \
  -Djava.awt.headless=true -Xmx2g -Dserver.port=8083 \
  -Dloader.path=lib -Djava.net.preferIPv4Stack=true \
  -jar "$(ls -1 $home/paster-run*.jar)" </dev/null >$home/console.log 2>&1 &

