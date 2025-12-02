Render deployment guide for AgroChain backend

Overview
- This document shows a quick path to deploy the `backend/` Node.js service on Render (https://render.com).
- It also covers creating a MongoDB Atlas cluster and configuring required environment variables.

Prerequisites
- Push your repository to GitHub (or connect your Git provider to Render).
- A Render account (free tier is sufficient for testing).
- A MongoDB Atlas account (free tier recommended) or another hosted MongoDB instance.

1) Create MongoDB Atlas cluster
- Sign in to https://cloud.mongodb.com and create a free tier cluster (select a nearby region).
- Create a database user (username and password) and record the credentials.
- Whitelist your IP for testing (or allow access from anywhere for quick testing, but restrict it later).
- Get the connection string (example):
  mongodb+srv://<user>:<password>@cluster0.abcd1.mongodb.net/agrochain?retryWrites=true&w=majority
- Replace `<user>` and `<password>` and optionally `agrochain` with the database name.

2) Prepare backend for Render
- Render auto-detects Node apps. The backend's `package.json` should have `start` script (it does: `npm start`).
- Ensure `backend/package.json` (or repo root if backend is root) is the one Render builds.
- If backend is in a subfolder (it is), you'll tell Render to use that folder as the root.

3) Push code to GitHub
- Commit & push your changes (including the `backend/` folder) to a repo and branch (e.g., `main`).

4) Create a new Web Service on Render
- In Render dashboard, choose "New" -> "Web Service".
- Connect your GitHub repo and select the branch.
- In "Root Directory" enter `backend` (so Render builds the backend folder).
- Environment: `Node`.
- Build Command: `npm install` (or `npm ci`).
- Start Command: `npm start`.
- Plan: Free (for testing).

5) Set environment variables on Render
- In the service settings -> Environment -> Environment Variables, add:
  - `MONGODB_URI` = your Atlas connection string
  - `JWT_SECRET` = a long random secret (do not use the default)
  - `NODE_ENV` = `production`
  - `PORT` = `3000` (optional; Render will set a port automatically but your code uses process.env.PORT)

6) Deploy and test
- Click "Create Web Service". Render will build and deploy.
- Once deployed, Render provides an HTTPS URL such as `https://agrochain-backend.onrender.com`.
- Update your Android app `PROD_BASE_URL` to `https://agrochain-backend.onrender.com/api/` and build a signed release.

Alternatives
- Railway (https://railway.app): similar quick deploy experience and built-in Postgres/MongoDB addons.
- Fly.io, DigitalOcean App Platform, Heroku (deprecated free tier) are other options.

Notes & Security
- Do NOT commit `JWT_SECRET` or `MONGODB_URI` to Git.
- Configure Atlas IP access and database user least-privilege.
- Use HTTPS in production and update `network_security_config.xml` to remove localhost allowances.

If you want, I can:
- Draft a `render.yaml` to declare the service (you still must connect repo on Render), or
- Walk you through creating the Atlas cluster and retrieving the connection string step-by-step, or
- Provide exact `PROD_BASE_URL` patch and sign the APK after you confirm the deployed URL.
