FROM alpine
ADD build/js/archive.tar.gz /tmp/deps.tar.gz
CMD ["echo"]
