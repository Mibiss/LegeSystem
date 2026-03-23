const express = require("express");
const { WebSocketServer } = require("ws");
const { spawn, spawnSync } = require("child_process");
const http = require("http");
const path = require("path");

// Compile Java files before starting the server
const javaDir = path.join(__dirname, "java");
console.log("Compiling Java files...");
const compile = spawnSync("javac", ["*.java"], {
    cwd: javaDir,
    stdio: "inherit",
});
if (compile.status !== 0) {
    console.error("Java compilation failed. Exiting.");
    process.exit(1);
}
console.log("Compilation successful.");

const app = express();
const server = http.createServer(app);
const wss = new WebSocketServer({ server });

app.set("view engine", "ejs");
app.set("views", path.join(__dirname, "views"));
app.use(express.static(path.join(__dirname, "public")));

app.get("/", (req, res) => {
    res.render("index.ejs");
});

wss.on("connection", (ws) => {
    const javaProcess = spawn("java", ["-cp", ".", "LegeSystem"], {
        cwd: javaDir,
    });

    javaProcess.stdout.on("data", (data) => {
        ws.send(JSON.stringify({ type: "output", data: data.toString() }));
    });

    javaProcess.stderr.on("data", (data) => {
        ws.send(JSON.stringify({ type: "output", data: data.toString() }));
    });

    javaProcess.on("close", () => {
        ws.send(JSON.stringify({ type: "exit" }));
        ws.close();
    });

    ws.on("message", (message) => {
        const input = JSON.parse(message);
        if (input.type === "input") {
            javaProcess.stdin.write(input.data);
        }
    });

    ws.on("close", () => {
        javaProcess.kill();
    });
});

const port = process.env.PORT || 3000;
server.listen(port, () => {
    console.log(`Server running on port ${port}`);
});
