FROM node:22-bullseye

RUN apt-get update && apt-get install -y default-jdk && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

RUN javac -encoding UTF-8 java/*.java

EXPOSE 3000

CMD ["node", "index.js"]
