#!/bin/sh
# determine CPU architecture
ARCH=$(uname -m)
if [ "$ARCH" = "x86_64" ]; then
  MARCH="i386:x86-64"
elif [ "$ARCH" = "arm64" ]; then
  MARCH="aarch64"
else
  echo "Unsupported architecture: $ARCH"
  exit 1
fi

# check whether gobjdump is on the path
if command -v gobjdump &> /dev/null; then
  OBJDUMP=gobjdump
else
  OBJDUMP=objdump
fi

exec ${OBJDUMP} -D -b binary -m ${MARCH} "$@"
