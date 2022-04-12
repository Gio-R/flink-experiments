// use std::env;
use std::io::{BufReader, BufRead};
use std::net::TcpListener;

fn main() {    
    let listener = TcpListener::bind("0.0.0.0:8080").unwrap();
    match listener.accept() {
        Ok((_socket, addr)) => {
            println!("new client: {:?}", addr);
            let mut reader = BufReader::new(_socket.try_clone().unwrap());
            let mut line = String::with_capacity(512);
            loop {
                println!("Waiting for line...");
                let result = reader.read_line(&mut line);
                match result {
                    Ok(_n) => println!("Received {}", line),
                    _ => {}
                }
                line.clear();
            }
        },
        Err(e) => println!("couldn't get client: {:?}", e),
    }
}