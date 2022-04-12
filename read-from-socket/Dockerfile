FROM rust:1.59

ENV host localhost
ENV port 8080

# 2. Copy the files in your machine to the Docker image
COPY ./ ./

# Build your program for release
RUN cargo build --release

# Run the binary
CMD ["sh", "-c", "./target/release/read-from-socket"]