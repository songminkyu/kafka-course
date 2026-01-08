home에 .bashrc 를 vi/vim/nano 이중 편집기를 선택하여, 하단 아래 내용을 입력 후 저장한다.
참고로 /home/smk 경로는 필자의 경로 이므로 참고바람

export CONFLUENT_HOME=/home/smk/confluent

export PATH=.:$PATH:/home/smk:$CONFLUENT_HOME/bin

/home/smk 위치에 아래 명령어 입력 후 입력한 설정값을 적용 되도록 한다.
. .bashrc 
