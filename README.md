# BatchSTT
is an open-source project that uses Whisper AI ASR Docker container to transcribe audio from videos automatically. It processes all videos in the given mounts and outputs the transcriptions in the desired format.

## Prerequisites
Before running BatchSTT, you need to have the following installed:
- Docker
- Docker Compose
## Getting Started
To get started, clone this repository to your local machine:

`git clone https://github.com/dinccey/batch_stt.git`

Then, navigate to the project directory and run the following command:

docker-compose up -d

This will start the BatchSTT application and Whisper AI ASR Docker container.
Environment VariablesThe following environment variables can be set in the docker-compose.yml file:
- ```SPRING_PROFILES_ACTIVE```: The active profile for Spring Boot. Possible values are ```local```, ```dev```, or ```prod```.
- ```WHISPER_ASR_URL```: The URL for Whisper AI ASR Docker container.
- ```OUTPUT_FORMAT```: The output format for transcriptions. Possible values are ```txt```, ```vtt```, ```srt```, ```json```, or ```tsv```.
- ```JOB_CRON```: The cron schedule for automatic transcription.
  Mounting DirectoriesTo mount directories, use the following syntax in the docker-compose.yml file:
- ```ASR_MODEL```: Which model to use: `tiny`, `base`, `small`, `medium`, `large`, `large-v1` and `large-v2`. Please note that `large` and `large-v2` are the same model.
  For English-only applications, the `.en` models tend to perform better, especially for the `tiny.en` and `base.en` models.
  We observed that the difference becomes less significant for the `small.en` and `medium.en` models.

For a CUDA enabled system (using nvidia docker on LINUX ONLY), use this image instead:
```
  openai-whisper-asr-webservice:
      image: onerahmet/openai-whisper-asr-webservice:latest-gpu
```
You can mount multiple directories by using different folder names:
```
volumes:
  - /path/to/local/directory:/mnt/videos/folder1:rw
  - /path/to/local/directory/videos:/mnt/videos/folder2:rw
```

You can mount multiple directories by using different folder names.
Built With- Spring Boot - The web framework used
- Whisper AI ASR - The speech recognition model used
