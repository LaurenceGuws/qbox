# Use a lightweight base image
FROM alpine:latest

# Set the working directory
WORKDIR /usr/local/bin

# Copy the qbox binary to the container
COPY qbox /usr/bin/qbox

# Ensure the binary is executable
RUN chmod +x /usr/bin/qbox

# Set the entrypoint to keep the container running indefinitely
CMD ["sh", "-c", "tail -f /dev/null"]

