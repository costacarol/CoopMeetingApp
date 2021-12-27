
Caso não possua o Docker instalado na sua maquina, digite o comando: 

$ sudo apt install docker.io

4. Para rodar a aplicação digite o comando: 

$ docker run --network=host --name coopmeeting -p 8080:8080 registry.heroku.com/api-rest-coop/web

4. Para acessar a documentaçao:

http://localhost:8080/swagger-ui.html

Para finalizar e realizar a exclusão do conteiner criado, digite os comandos:

$ docker stop calculator

$ docker rm coopmeeting