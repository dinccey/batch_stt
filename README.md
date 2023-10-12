# BatchSTT
Batch Speech to Text is a project that uses Whisper AI ASR Docker container to transcribe audio from videos automatically. It processes all videos in the given mounts and outputs the transcriptions in the desired format.

## Prerequisites
Before running BatchSTT, you need to have the following installed:
- Docker
- Docker compose
- Maven and JDK 17 in system PATH
## Getting Started
To get started, clone this repository to your local machine:

`git clone https://github.com/dinccey/batch_stt.git`

Then to build and run (don't forget to customize environment variables in docker-compose.yml):

`docker-compose build`

`docker-compose up -d`

This will start the BatchSTT application and Whisper AI ASR Docker container. You can run Whisper container anywhere but only one instance at a time is supported at the moment.

## Environment Variables

The following environment variables can be set in the docker-compose.yml file:
- ```MP3_SAVE```: true or false, (default false) save mp3 of video in the same folder.
- ```SPRING_PROFILES_ACTIVE```: The active profile for Spring Boot. Possible values are ```local```, ```dev```, or ```prod```.
- ```WHISPER_ASR_URL```: The URL for Whisper AI ASR Docker container.
- ```OUTPUT_FORMAT```: The output format for transcriptions. Possible values are ```txt```, ```vtt```, ```srt```, ```json```, or ```tsv```.
- ```JOB_CRON```: The cron schedule for automatic transcription.
- ```ASR_MODEL```: Which model to use: `tiny`, `base`, `small`, `medium`, `large`, `large-v1` and `large-v2`. Please note that `large` and `large-v2` are the same model.
  For English-only applications, the `.en` models tend to perform better, especially for the `tiny.en` and `base.en` models.
  We observed that the difference becomes less significant for the `small.en` and `medium.en` models.
For a CUDA enabled system (using nvidia docker on LINUX ONLY), use this image instead:
```
  openai-whisper-asr-webservice:
      image: onerahmet/openai-whisper-asr-webservice:latest-gpu
```
In case `latest` doesn't work, use `v1.1.1` or `v1.1.1-gpu` instead

You can mount multiple directories by using different folder names:
```
volumes:
  - /path/to/local/directory:/mnt/videos/folder1:rw
  - /path/to/local/directory/videos:/mnt/videos/folder2:rw
  # filter file
  - /var/home/vaslim/Container/filter.txt:/etc/filter.txt:r
```
To start the job manually there is an admin endpoint available. First, login with (currently) hardcoded username and password using the following JSON body:

```
{
  "username": "admin",
  "password": "mysecurepassword"
}
```
on `/api/v1/auth/login`

Then, on call GET on `/api/v1/admin/run`. The response will be blocked until all media is processed so it's a bad idea to use it other than for test runs.
The functionality of the endpoints is likely to be extended.

To import endpoints, use OpenAPI on `/v3/api-docs`

## Filtering
For post-processing of transcriptions, prepare a text file with key:value pairs where key is original word and value is replacement word. Example file is in the project.
Before saving filtered transcriptions, a backup is saved with the hash of its filter.
When the filter file is changed in any way, all filtering will be re-done.

This software is provided as-is. There may be bugs and so on.