ARG IMG_TAG
FROM bitnami/spark:${IMG_TAG}
USER root
RUN  mkdir /var/lib/apt/lists /var/cache/apt/archives
RUN apt-get autoremove && \
    apt-get update && \
    apt-get upgrade -y
RUN install_packages curl

