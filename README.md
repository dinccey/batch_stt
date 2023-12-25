# BatchSTT
Batch Speech to Text is a project that uses Whisper AI ASR Docker container to transcribe audio from videos automatically. It processes all videos in the given mounts and outputs the transcriptions in the desired format.

## Prerequisites
Before running BatchSTT, you need to have the following installed:
- Docker
- Docker compose
- A Linux system with Nvidia Docker for GPU inference (optional)
## Getting Started
To get started, clone this repository to your local machine:

`git clone https://github.com/dinccey/batch_stt.git`

Then to build and run (don't forget to customize environment variables in docker-compose.yml):

`docker-compose build`

`docker-compose up -d`

This will start the BatchSTT application and Whisper AI ASR Docker container. The main backend now supports multiple (virtually unlimited) Whisper instances. They can be added (POST api/v1/instances/add) after logging in with a user on the auth/login endpoint. All reachable (automatically checked) Whisper instances are then added to a connection pool and used for transcription. Before transcription, each video is converted to a 128kbps MP3 file. This process happens on the backend and could cause a bottleneck if a large number of Whisper instances is supported by weak hardware on the main backend.

## Environment Variables

There is a ```.env``` file available, modify values in it. 
NOTE: start the db docker-compose if you don't plan on connecting to a existing db.

The following environment variables (and several others, see .env) can be set in the docker-compose.yml (or .env) file:
- ```MP3_SAVE```: true or false, (default false) save mp3 of video in the same folder.
- ```EXCLUDED_PATHS```: Comma separated list of paths that should not be processed
- ```SPRING_PROFILES_ACTIVE```: The active profile for Spring Boot. Possible values are ```local```, ```dev```, or ```prod```.
- ```WHISPER_ASR_URLS```: The URLs for Whisper AI ASR Docker container, comma separated for multiple values. They can and should be dynamically added per user using the API but hardcoding here is also supported.
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

To import all endpoints with sample JSON, use OpenAPI on `/v3/api-docs`

## Filtering (optional)
For post-processing of transcriptions, prepare a text file with key:value pairs where key is original word and value is replacement word. Example file is in the project.
Before saving filtered transcriptions, a backup is saved with the hash of its filter.
When the filter file is changed in any way, all filtering will be re-done. To disable filtering, put a wrong path (for now)

This software is provided as-is. There may be bugs and so on.