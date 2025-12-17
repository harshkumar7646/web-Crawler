# web-crawler
Making a custom  Search Engine

Flow of Execution
->WebCrawler starts the application
->Initial seed URLs are added to URL Storage
->Thread Handler creates multiple worker threads
->Each thread:
  ->Takes a URL from storage
  ->Fetches page content
  ->Extracts new URLs
  ->Sends new URLs back to storage

->Process continues until:
->No URLs are left
OR a predefined crawl limit is reached
