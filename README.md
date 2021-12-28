
Caso não possua o Docker instalado na sua maquina, digite o comando: 

`$ sudo apt install docker.io`

1. Para baixar a imagem no docker:

`$ docker pull costacarolis/coop-api-rest`

1. Para rodar a aplicação digite o comando: 

`$ docker run --network=host --name coopmeeting -p 8080:8080 costacarolis/coop-api-rest`

2. Para acessar a documentaçao:

http://localhost:8080/swagger-ui.html

3. Para finalizar e realizar a exclusão do container criado, digite os comandos:

`$ docker stop coopmeeting`

`$ docker rm coopmeeting`
